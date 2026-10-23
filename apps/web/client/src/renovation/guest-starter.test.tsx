import { afterEach, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { GuestStarterJourney, GUEST_STORAGE_KEY, buildGuestAttachmentRequest, guestAttemptCount, guestUnattachedAttemptCount, markGuestAttemptsAttached } from "./guest-starter";

afterEach(() => {
  cleanup();
  localStorage.clear();
});

it("starts real guest practice without authentication and restores the saved draft/feedback", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  expect(screen.getByRole("heading", { name: "Practise what needs attention." })).toBeInTheDocument();
  expect(screen.queryByLabelText("Your current German level")).toBeNull();
  expect(screen.queryByLabelText("Usual session")).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  expect(screen.getByText("Question 1 of 5")).toBeInTheDocument();
  expect(screen.queryByText("Type the plural.")).toBeNull();
  expect(screen.queryByRole("textbox")).toBeNull();
  expect(screen.queryByText(/Shorter session:/)).toBeNull();
  expect(screen.getByRole("radio", { name: "Berufe" })).toBeInTheDocument();
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  expect(screen.getByText("Correct")).toBeInTheDocument();
  expect(screen.queryByText(/server-confirmed|confirmed mastery/i)).not.toBeInTheDocument();
  expect(guestAttemptCount()).toBe(1);
  cleanup();

  render(<GuestStarterJourney onAuth={onAuth} />);
  fireEvent.click(screen.getByRole("button", { name: "Continue session" }));
  expect(screen.getByText("Correct")).toBeInTheDocument();
  expect(screen.queryByRole("textbox")).toBeNull();
  expect(onAuth).not.toHaveBeenCalled();
});

it("lets a guest finish text-based exercises by tapping choices without changing saved answer types", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  expect(screen.queryByRole("textbox")).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  const saved = JSON.parse(localStorage.getItem(GUEST_STORAGE_KEY)!);
  expect(saved.attempts[0].answer).toEqual({type:"short_answer",text:"Berufe"});
  fireEvent.click(screen.getByRole("button", { name: "Continue" }));
  fireEvent.click(screen.getByRole("button", { name: "Skip" }));
  fireEvent.click(screen.getByRole("radio", { name: "ins" }));
  expect(screen.getByRole("radio",{name:"ins"})).toBeChecked();
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  expect(JSON.parse(localStorage.getItem(GUEST_STORAGE_KEY)!).attempts[1].answer).toEqual({type:"cloze",values:[{slotId:"preposition",text:"ins"}]});
});

it("offers account creation only after useful guest practice and never attaches automatically", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  fireEvent.click(screen.getByRole("button", { name: "Continue" }));
  for (let i = 0; i < 4; i++) fireEvent.click(screen.getByRole("button", { name: "Skip" }));

  expect(screen.getByRole("heading", { name: "Starter session complete" })).toBeInTheDocument();
  expect(screen.getByText("1 answered · 4 skipped")).toBeInTheDocument();
  expect(screen.getByRole("heading", { name: "Keep your progress" })).toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Create account" }));
  expect(onAuth).toHaveBeenCalledWith("register");
  expect(guestAttemptCount()).toBe(1);
});

it("keeps sign-in optional before the first exercise", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  fireEvent.click(screen.getByRole("button", { name: "I already have an account" }));
  expect(onAuth).toHaveBeenCalledWith("sign-in");
  expect(localStorage.getItem(GUEST_STORAGE_KEY)).toBeNull();
});

it("builds account-scoped replay-safe attachment data without trusting the local evaluation", () => {
  const subject="00000000-0000-4000-8000-000000000020";
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  fireEvent.click(screen.getByRole("button", { name: "Hint" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  expect(screen.getByText("Correct")).toBeInTheDocument();
  expect(screen.queryByText(/Assisted answer|Looks correct locally/)).toBeNull();
  expect(screen.queryByRole("radio")).toBeNull();
  const request=buildGuestAttachmentRequest(subject);
  expect(request?.attempts).toHaveLength(1);
  expect(request?.attempts[0]).toMatchObject({exerciseRevision:1,answer:{type:"short_answer",text:"Berufe"},assistance:["hint"]});
  expect(request?.attempts[0]).not.toHaveProperty("evaluation");
  expect(guestUnattachedAttemptCount(subject)).toBe(1);
  markGuestAttemptsAttached(subject,[request!.attempts[0].attemptId]);
  expect(guestUnattachedAttemptCount(subject)).toBe(0);
  expect(guestUnattachedAttemptCount("00000000-0000-4000-8000-000000000021")).toBe(1);
  expect(buildGuestAttachmentRequest(subject)).toBeNull();
});

it("preserves a damaged guest record until the learner explicitly resets it", () => {
  localStorage.setItem(GUEST_STORAGE_KEY, "{broken");
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  expect(screen.getByRole("alert")).toHaveTextContent("has been preserved");
  expect(localStorage.getItem(GUEST_STORAGE_KEY)).toBe("{broken");
});


it("finishing guest practice avoids a second account prompt and honestly offers only a repeat starter", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  fireEvent.click(screen.getByRole("button", { name: "Continue" }));
  for (let i = 0; i < 4; i++) fireEvent.click(screen.getByRole("button", { name: "Skip" }));
  expect(screen.getByRole("heading", { name: "Keep your progress" })).toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Finish" }));
  expect(screen.queryByRole("heading", { name: "Save across devices" })).toBeNull();
  expect(screen.queryByRole("button", { name: "Change setup" })).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Repeat the five questions" }));
  expect(screen.getByText("Question 1 of 5")).toBeInTheDocument();
  // A new session may offer one optional account prompt when completed again.
});
