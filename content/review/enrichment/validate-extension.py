"""Read-only, exact-source validator for independent review batches 0050-0175.

This does not approve, apply, publish or activate any correction.
"""
import hashlib
import json
import subprocess
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
HERE = Path(__file__).resolve().parent
FINAL = "b7a6507e135895b1ebb671b73538a9a6d3742304"

def load(path):
    return json.loads(path.read_text(encoding="utf-8"))

def check(condition, message):
    if not condition:
        raise ValueError(message)

def pinned(batch, suffix, expected):
    name = "content/enrichment/batches/" + batch + "." + suffix + ".json"
    raw = subprocess.check_output(["git", "show", FINAL + ":" + name], cwd=ROOT)
    digest = hashlib.sha256(raw.replace(b"\r\n", b"\n")).hexdigest()
    check(digest == expected, name + " pinned SHA-256 mismatch")
    return json.loads(raw)

def run():
    queue = load(HERE / "extension-queue.json")
    coverage = load(HERE / "extension-coverage.json")
    reference = load(HERE / "structural-b7a6507e1358.json")
    frozen = load(HERE / "queue.json")
    check(queue["snapshot"] == coverage["snapshot"] == reference["snapshot"] == FINAL,
          "Extension source snapshot mismatch")
    check(queue["originalQueueSnapshot"] == frozen["snapshot"] and
          len(frozen["entries"]) == 49 and all(e["state"] == "complete" for e in frozen["entries"]),
          "Original 49 reviews must remain frozen and complete")
    check(len(queue["entries"]) == 126 and len(reference["inputs"]) == 175 and
          reference["canonicalValidatorExitCode"] == 0, "Invalid full corpus index")
    check(all(reference["inputs"][i]["batchId"] == frozen["entries"][i]["batchId"] and
              reference["inputs"][i]["catalogSha256"] == frozen["entries"][i]["catalogSha256"]
              for i in range(49)), "Original and final sources differ")
    check(queue["active"] is False and queue["publicationApproved"] is False and
          coverage["active"] is False and coverage["candidateReady"] is False and
          coverage["publicationApproved"] is False, "Publication cannot be active")
    statuses = Counter()
    seen_variants = set()
    seen_exercises = set()
    reviewed = 0
    proposed = 0
    total = 0
    for number, entry in enumerate(queue["entries"], start=50):
        batch = "legacy-" + str(number).zfill(4)
        expected = reference["inputs"][number - 1]
        check(entry["batchId"] == expected["batchId"] == batch and
              entry["catalogSha256"] == expected["catalogSha256"] and
              entry["reportSha256"] == expected["reportSha256"] and
              entry["key"] == batch + ":" + entry["catalogSha256"] and
              entry["candidateReady"] is False, batch + " wrong queue key")
        cat = pinned(batch, "catalog", entry["catalogSha256"])
        report = pinned(batch, "report", entry["reportSha256"])
        check(cat["publicationApproved"] is False and
              report["catalogSha256"] == entry["catalogSha256"] and
              report["state"] == "ready-for-review", batch + " author report mismatch")
        source = {}
        for t in cat["targets"]:
            for v in t["variants"]:
                key = v["variantKey"]
                ex = v["exercise"]
                check(key not in seen_variants and ex["id"] not in seen_exercises,
                      batch + " duplicated question identity")
                seen_variants.add(key)
                seen_exercises.add(ex["id"])
                check(ex["targetId"] == t["id"], key + " wrong exercise target")
                source[key] = (t, v)
        total += len(source)
        artifact_path = HERE / (batch + ".findings.json")
        keys = set()
        status = "pending"
        if artifact_path.exists():
            findings = load(artifact_path)
            status = findings["state"]
            check(status in ("partial", "complete") and
                  findings["snapshot"] == FINAL and
                  findings["batchId"] == batch and
                  findings["catalogSha256"] == entry["catalogSha256"],
                  batch + " findings binding mismatch")
            for row in findings["reviewed"]:
                key = row["variantKey"]
                check(key in source and key not in keys, batch + " duplicate/unknown review")
                keys.add(key)
                t, v = source[key]
                check(row["targetId"] == t["id"] and
                      row["exerciseId"] == v["exercise"]["id"], key + " wrong review IDs")
                for field in ("disposition", "finding", "rubricAndLanguage", "provenance", "levelAndIdentity"):
                    check(isinstance(row.get(field), str) and row[field].strip(),
                          key + " missing editorial field " + field)
                ex = v["exercise"]
                choices = ex.get("options") or [o for s in ex["slots"] for o in s["options"]]
                checks = row.get("optionAssessments", [])
                check(len(choices) == len(checks) and len(choices) > 0 and
                      all(isinstance(a, str) and
                          a.casefold().startswith(o["text"].casefold())
                          for o, a in zip(choices, checks)),
                      key + " failed substantive per-option coverage identity")
            check(status != "complete" or keys == set(source), batch + " incomplete complete review")
        check(entry["state"] == status and sorted(entry["reviewedVariantKeys"]) == sorted(keys),
              batch + " queue not synchronized with reviews")
        statuses[status] += 1
        reviewed += len(keys)
        cov_row = coverage["batches"][number - 50]
        check(cov_row["batchId"] == batch and cov_row["state"] == status and
              cov_row["reviewedQuestions"] == len(keys) and
              cov_row["candidateReady"] is False, batch + " coverage mismatch")
        overlay_file = HERE / "extension-proposals" / (batch + ".overlay.json")
        if overlay_file.exists():
            overlay = load(overlay_file)
            check(overlay["snapshot"] == FINAL and overlay["batchId"] == batch and
                  overlay["catalogSha256"] == entry["catalogSha256"] and
                  overlay["active"] is False and overlay["candidateReady"] is False,
                  batch + " proposal is not pinned and inactive")
            patched = set()
            for p in overlay["patches"]:
                key = p["variantKey"]
                check(key in keys and key not in patched and
                      p["status"] == "proposed-needs-recheck", key + " invalid proposal state")
                patched.add(key)
                v = source[key][1]
                ex = v["exercise"]
                check(ex["id"] == p["exerciseId"] and ex["prompt"] == p["expectedPrompt"],
                      key + " proposed edit source mismatch")
                choice_set = ex.get("options") or [o for s in ex["slots"] for o in s["options"]]
                accepted = v["rubric"]["acceptedAnswers"][0]
                answer_id = accepted["optionId"] if accepted["type"] == "choice" else accepted["selections"][0]["optionId"]
                answer = next((x["text"] for x in choice_set if x["id"] == answer_id), None)
                check(answer is not None and p["proposedPrompt"].count("___") == 1 and
                      p["proposedPrompt"].replace("___", answer) == p["proposedCompletedAnswer"],
                      key + " proposed completion/key mismatch")
                for field in ("proposedHint", "proposedExplanation"):
                    if field in p:
                        check(set(p[field]) == {"en", "de"} and all(p[field].values()),
                              key + " incomplete bilingual proposal")
                proposed += 1
    check(total == 4146 and len(seen_variants) == total,
          "Full extension corpus size/unique identities mismatch")
    check(len(coverage["batches"]) == 126 and
          coverage["totalQuestions"] == total and
          coverage["reviewedQuestions"] == reviewed and
          coverage["completedBatches"] == statuses["complete"] and
          coverage["partialBatches"] == statuses["partial"] and
          coverage["pendingBatches"] == statuses["pending"] and
          coverage.get("inactiveCorrectionProposals", 0) == proposed,
          "Saved extension coverage counters mismatch")
    print("Extension review validation passed:", statuses["complete"],
          "complete batches,", reviewed, "/", total,
          "AI-reviewed questions,", proposed, "inactive proposals; no publication")

if __name__ == "__main__":
    run()
