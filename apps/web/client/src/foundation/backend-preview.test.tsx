import { describe, it, expect, vi, afterEach } from "vitest";
import { render, screen, fireEvent, cleanup, waitFor } from "@testing-library/react";
import { SessionSchema, AttemptBatchResponseSchema, type Attempt } from "@german-master/contracts";
import sample from "@german-master/contracts/examples/session.json";
import response from "@german-master/contracts/examples/attempt-response.json";
import BackendPreview from "./backend-preview";
import { localFoundationApi, type FoundationApi } from "./api";

afterEach(() => { cleanup(); vi.unstubAllGlobals(); });
const session = SessionSchema.parse(sample);
const acknowledgment = AttemptBatchResponseSchema.parse(response).acknowledgments[0];

describe("server-confirmed preview", () => {
  it("retries an ambiguous timeout with the same immutable payload, then shows server feedback", async () => {
    const submit = vi.fn<FoundationApi["submit"]>().mockRejectedValueOnce(Error("timeout"))
      .mockImplementation(async input => ({ ...acknowledgment, attemptId: input.attemptId }));
    render(<BackendPreview api={{ createSession: async () => session, submit }} />);
    await screen.findByLabelText("Your answer");
    fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "berufe" } });
    fireEvent.click(screen.getByText("Check answer"));
    await screen.findByRole("alert");
    expect(screen.getByLabelText("Your answer")).toBeDisabled();
    fireEvent.click(screen.getByText("Retry"));
    await screen.findByRole("status");
    expect(submit.mock.calls[0][0]).toEqual(submit.mock.calls[1][0]);
    // The mock server deliberately says correct for a lowercase answer: UI trusts server, never grades locally.
    expect(screen.getByRole("status")).toHaveTextContent("Correct");
    expect(screen.getByText("berufe")).toBeInTheDocument();
    expect(screen.getByText("Berufe")).toBeInTheDocument();
    fireEvent.click(screen.getByText("Continue"));
    expect(screen.getByText("Check answer")).toBeDisabled();
    expect(screen.getByRole("heading")).toHaveFocus();
  });
  it("shows a permanent rejection without treating it as feedback or permitting another answer", async () => {
    render(<BackendPreview api={{ createSession: async () => session, submit: async input => ({ attemptId: input.attemptId, status: "rejected",
      error: { code: "question_already_answered", message: "Rejected", requestId: crypto.randomUUID(), retryable: false } }) }} />);
    await screen.findByLabelText("Your answer");
    fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
    fireEvent.click(screen.getByText("Check answer"));
    await screen.findByRole("alert");
    expect(screen.getByText("Retry")).toBeDisabled();
    expect(screen.queryByText("Continue")).not.toBeInTheDocument();
  });
  it("validates acknowledgment identity before displaying a result", async () => {
    vi.stubGlobal("fetch", vi.fn().mockResolvedValue({ ok: true, json: async () => ({ apiVersion: "v2", acknowledgments: [acknowledgment] }) }));
    const input: Attempt = { attemptId: crypto.randomUUID(), sessionQuestionId: session.questions[0].id, exerciseRevision: 1,
      deviceId: crypto.randomUUID(), answer: { type: "short_answer", text: "Berufe" }, assistance: [], answeredAt: "2026-10-03T12:00:00Z", clientSequence: 0 };
    await expect(localFoundationApi().submit(input)).rejects.toThrow("linkage mismatch");
  });
  it("records hint assistance and confirms a complete five-question session", async () => {
    const submit = vi.fn<FoundationApi["submit"]>().mockImplementation(async input => ({ ...acknowledgment, attemptId: input.attemptId }));
    render(<BackendPreview api={{ createSession: async () => session, submit }} />);
    await screen.findByLabelText("Your answer");
    const hints = screen.getByText("Hint").closest("details")!;
    hints.open = true;
    fireEvent(hints, new Event("toggle"));
    for (let i=0; i<5; i++) {
      if(i===0) fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
      if(i===1) fireEvent.click(screen.getByLabelText("dem"));
      if(i===2) fireEvent.change(screen.getByLabelText("Präposition"), { target: { value: "in das" } });
      if(i===3) for(const word of ["weil","ich","heute","im","Büro","arbeite"]) fireEvent.click(screen.getByRole("button", {name:word}));
      if(i===4) for(const [label,value] of [["du","arbeitest"],["ihr","arbeitet"]]) fireEvent.change(screen.getByLabelText(label), { target: { value } });
      fireEvent.click(screen.getByText("Check answer"));
      await screen.findByText("Continue");
      fireEvent.click(screen.getByText("Continue"));
    }
    await waitFor(() => expect(screen.getByRole("heading")).toHaveTextContent("Session complete"));
    expect(submit.mock.calls[0][0].assistance).toEqual(["hint"]);
    expect(submit.mock.calls.slice(1).every(([input]) => input.assistance.length===0)).toBe(true);
  });
});
