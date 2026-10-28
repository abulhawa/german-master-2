import { afterEach, expect, it } from "vitest";
import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { GuestStarterJourney, TEMPORARY_KEY } from "./guest-starter";

afterEach(() => {
  cleanup();
  localStorage.clear();
  sessionStorage.clear();
  window.history.replaceState(null, "", "/");
});

it("continues through the finite B1 pool and offers a topic change after every item", () => {
  render(<GuestStarterJourney onAuth={() => undefined} />);
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
  const started = JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!);
  expect(started.session.questions).toHaveLength(65);
  expect(new Set(started.session.questions.map((question: { id: string }) => question.id)).size).toBe(65);
  expect(screen.getByText("Question 1")).toBeInTheDocument();
  expect(screen.queryByText(/of 5/)).not.toBeInTheDocument();

  for (let index = 0; index < 65; index++) {
    fireEvent.click(screen.getByRole("button", { name: "Skip" }));
    if (index < 64) expect(screen.getByText(`Question ${index + 2}`)).toBeInTheDocument();
  }
  expect(screen.getByRole("heading", { name: "You've practised the available questions" })).toBeInTheDocument();
  expect(screen.getByRole("button", { name: "Choose another topic" })).toBeEnabled();
  expect(screen.queryByRole("button", { name: "Skip" })).not.toBeInTheDocument();
});
