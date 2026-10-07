import { afterEach, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { GuestStarterJourney, GUEST_STORAGE_KEY, guestAttemptCount } from "./guest-starter";

afterEach(() => {
  cleanup();
  localStorage.clear();
});

it("starts real guest practice without authentication and restores the saved draft/feedback", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  expect(screen.getByRole("heading", { name: "Practise what needs attention." })).toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  expect(screen.getByRole("status")).toHaveTextContent("Guest practice · saved on this device");
  expect(screen.getByText("Shorter session: 5 reviewed questions are available.")).toBeInTheDocument();
  fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  expect(screen.getByText("Looks correct locally")).toBeInTheDocument();
  expect(screen.queryByText(/server-confirmed|confirmed mastery/i)).not.toBeInTheDocument();
  expect(guestAttemptCount()).toBe(1);
  cleanup();

  render(<GuestStarterJourney onAuth={onAuth} />);
  fireEvent.click(screen.getByRole("button", { name: "Continue session" }));
  expect(screen.getByText("Looks correct locally")).toBeInTheDocument();
  expect(screen.getByText("Berufe")).toBeInTheDocument();
  expect(onAuth).not.toHaveBeenCalled();
});

it("offers account creation only after useful guest practice and never attaches automatically", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  fireEvent.click(screen.getByRole("button", { name: "Try German Master" }));
  fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
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

it("preserves a damaged guest record until the learner explicitly resets it", () => {
  localStorage.setItem(GUEST_STORAGE_KEY, "{broken");
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  expect(screen.getByRole("alert")).toHaveTextContent("has been preserved");
  expect(localStorage.getItem(GUEST_STORAGE_KEY)).toBe("{broken");
});
