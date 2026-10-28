import { beforeEach, afterEach, expect, it, vi } from "vitest";
import { act, cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { GuestStarterJourney, GUEST_STORAGE_KEY, TEMPORARY_KEY, buildGuestAttachmentRequest, guestAttemptCount, guestUnattachedAttemptCount, markGuestAttemptsAttached } from "./guest-starter";
import { APPEARANCE_KEY } from './appearance';

vi.mock('../../../../../content/guest/practice.json',async()=>{
 const {default:starter}=await import('../../../../../content/guest/starter.json');
 return {default:{packs:[{level:'B1',learningContext:'general',topicId:'30000000-0000-4000-8000-000000000600',title:{en:'Grammar foundations',de:'Grammatik-Grundlagen'},...starter}]}};
});
beforeEach(()=>{vi.spyOn(Math,'random').mockReturnValue(0.99999);});
it('persists appearance without an account and preserves temporary practice on cancellation',()=>{
 render(<GuestStarterJourney onAuth={vi.fn()}/>);
 fireEvent.click(screen.getByRole('button',{name:'Settings',exact:true}));
 fireEvent.click(screen.getByText('Language and appearance'));
 fireEvent.change(screen.getByRole('combobox',{name:'Theme'}),{target:{value:'dark'}});
 expect(localStorage.getItem(APPEARANCE_KEY)).toBe('dark');
 fireEvent.click(screen.getByRole('button',{name:'Home',exact:true}));
 fireEvent.click(screen.getByRole('button',{name:'Start practice'}));
 fireEvent.click(screen.getByRole('radio',{name:'Berufe'}));
 fireEvent.click(screen.getByRole('button',{name:'Close practice'}));
 fireEvent.click(screen.getByRole('button',{name:'Keep practising'}));
 expect(screen.getByRole('radio',{name:'Berufe'})).toBeChecked();
 cleanup();render(<GuestStarterJourney onAuth={vi.fn()}/>);
 expect(document.querySelector('main')).toHaveAttribute('data-theme','dark');
 expect(screen.getByRole('radio',{name:'Berufe'})).toBeChecked();
});

afterEach(() => {
  cleanup();
  localStorage.clear();
  sessionStorage.clear();vi.restoreAllMocks();
  window.history.replaceState(null, "", "/");
});

it("starts real guest practice without authentication and restores the saved draft/feedback", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  expect(screen.getByRole("heading", { name: "A little German. A little more confidence." })).toBeInTheDocument();
  expect(screen.queryByRole("button", { name: "Try German Master" })).toBeNull();
  expect(screen.queryByLabelText("Your current German level")).toBeNull();
  expect(screen.queryByLabelText("Usual session")).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
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
  expect(window.location.hash).toBe("#/learn/practice");
  expect(screen.getByText("Correct")).toBeInTheDocument();
  expect(screen.queryByRole("textbox")).toBeNull();
  expect(onAuth).not.toHaveBeenCalled();
});

it("lets a guest finish text-based exercises by tapping choices without changing saved answer types", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  expect(screen.queryByRole("textbox")).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  const saved = JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!);
  expect(saved.attempts[0].answer).toEqual({type:"short_answer",text:"Berufe"});
  fireEvent.click(screen.getByRole("button", { name: "Continue" }));
  fireEvent.click(screen.getByRole("button", { name: "Skip" }));
  fireEvent.click(screen.getByRole("radio", { name: "ins" }));
  expect(screen.getByRole("radio",{name:"ins"})).toBeChecked();
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  expect(JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!).attempts[1].answer).toEqual({type:"cloze",values:[{slotId:"preposition",text:"ins"}]});
});

it("requires an account only when opening account-dependent pages and allows returning without signing in", () => {
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  expect(screen.queryByRole("button", { name: "Sign in" })).toBeNull();
  for (const page of ["Progress", "Settings"]) {

    fireEvent.click(screen.getByRole("button", { name: page, exact: true }));
    expect(screen.getByRole("heading", { name: page })).toBeInTheDocument();
    expect(screen.getByRole("status")).toHaveTextContent(page === "Settings" ? "Sign in to keep your progress." : "This page requires an account");
    expect(screen.getByRole("button", { name: "Sign in" })).toBeEnabled();
    expect(screen.getByRole("button", { name: "Create account" })).toBeEnabled();
    fireEvent.click(screen.getByRole("button", { name: "Back to practice" }));
    expect(screen.getByRole("button", { name: "Start practice" })).toBeEnabled();
  }
  expect(onAuth).not.toHaveBeenCalled();
  fireEvent.click(screen.getByRole("button", { name: /Account|Settings/, exact: true }));
  fireEvent.click(screen.getByRole("button", { name: "Create account" }));
  expect(onAuth).toHaveBeenCalledWith("register");
});

it("preserves the full draft through explicit save, cancelled authentication and reload",()=>{
 const auth=vi.fn();render(<GuestStarterJourney onAuth={auth}/>);
 fireEvent.click(screen.getByRole('button',{name:'Start practice'}));
 fireEvent.click(screen.getByRole('radio',{name:'Berufe'}));
 const draft=sessionStorage.getItem(TEMPORARY_KEY);
 fireEvent.click(screen.getByRole('button',{name:'Account',exact:true}));
 fireEvent.click(screen.getByRole('button',{name:'Sign in',exact:true}));
 expect(auth).toHaveBeenCalledWith('sign-in');
 expect(localStorage.getItem(GUEST_STORAGE_KEY)).toBe(draft);
 cleanup();render(<GuestStarterJourney onAuth={auth}/>);
 expect(screen.getByRole('radio',{name:'Berufe'})).toBeChecked();
 expect(sessionStorage.getItem(TEMPORARY_KEY)).toBe(draft);
});

it("allows sign-in even if saved practice is damaged", () => {
  localStorage.setItem(GUEST_STORAGE_KEY, "{broken");
  const onAuth = vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  fireEvent.click(screen.getByRole("button", { name: "Sign in" }));
  expect(onAuth).toHaveBeenCalledWith("sign-in");
  expect(localStorage.getItem(GUEST_STORAGE_KEY)).toBe("{broken");
});

it("builds account-scoped replay-safe attachment data without trusting the local evaluation", () => {
  const subject="00000000-0000-4000-8000-000000000020";
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
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

it("keeps the interface language selector reachable from guest home", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  fireEvent.click(screen.getByRole("button", { name: "Continue" }));
  for (let i = 0; i < 4; i++) fireEvent.click(screen.getByRole("button", { name: "Skip" }));
  fireEvent.click(screen.getByRole("button", { name: "Finish" }));

  fireEvent.click(screen.getByRole("button", { name: "Settings" }));
  const language = screen.getByRole("combobox", { name: "Interface language" });
  fireEvent.change(language, { target: { value: "de" } });
  expect(document.querySelector("main")).toHaveAttribute("lang", "de");
  fireEvent.click(screen.getByRole("button", { name: "Startseite" }));
  expect(screen.getByRole("heading", { name: "Ein wenig Deutsch. Ein Stück sicherer." })).toBeInTheDocument();
  expect(JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!).locale).toBe("de");
});

it("finishes and repeats practice without an account promotion", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  fireEvent.click(screen.getByRole("button", { name: "Check answer" }));
  fireEvent.click(screen.getByRole("button", { name: "Continue" }));
  for (let i = 0; i < 4; i++) fireEvent.click(screen.getByRole("button", { name: "Skip" }));
  expect(screen.getByRole("heading", { name: "Session complete" })).toBeInTheDocument();
  expect(screen.queryByRole("button", { name: "Sign in" })).toBeNull();
  expect(screen.queryByRole("button", { name: "Create account" })).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Finish" }));
  cleanup();
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  expect(screen.queryByRole("heading", { name: "Save across devices" })).toBeNull();
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
  expect(screen.getByText("Question 1 of 5")).toBeInTheDocument();
});

it("localizes account gating without writing or discarding practice", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Settings" }));
  fireEvent.change(screen.getByRole("combobox", { name: "Interface language" }), { target: { value: "de" } });
  fireEvent.click(screen.getByRole("button", { name: "Fortschritt" }));
  expect(screen.getByRole("status")).toHaveTextContent("Für diese Seite brauchst du ein Konto");
  expect(screen.getByRole("button", { name: "Anmelden" })).toBeEnabled();
  expect(screen.getByRole("button", { name: "Konto erstellen" })).toBeEnabled();
  expect(guestAttemptCount()).toBe(0);
});

it("keeps primary navigation on account gates and makes Topics available without sign-in", () => {
  const onAuth=vi.fn();
  render(<GuestStarterJourney onAuth={onAuth} />);
  expect(screen.getByRole("button",{name:"Home",exact:true})).toHaveAttribute("aria-current","page");
  fireEvent.click(screen.getByRole("button",{name:"Topics",exact:true}));
  expect(screen.getByRole("button",{name:"Topics",exact:true})).toHaveAttribute("aria-current","page");
  expect(screen.getByRole("heading",{name:"Grammar foundations"})).toBeInTheDocument();
  expect(screen.getByRole("button",{name:"Select topic"})).toBeEnabled();
  expect(screen.queryByRole("button",{name:"Sign in",exact:true})).toBeNull();
  fireEvent.click(screen.getByRole("button",{name:"Progress",exact:true}));
  expect(screen.getByRole("navigation",{name:"Main navigation"})).toBeInTheDocument();
  fireEvent.click(screen.getByRole("button",{name:"Home",exact:true}));
  fireEvent.click(screen.getByRole("button",{name:"Start practice",exact:true}));
  expect(screen.queryByRole("navigation")).toBeNull();
  expect(onAuth).not.toHaveBeenCalled();
});

it('browser Back requires an explicit choice and keeps the current answer when cancelled',async()=>{
 render(<GuestStarterJourney onAuth={vi.fn()}/>);
 fireEvent.click(screen.getByRole('button',{name:'Start practice'}));
 fireEvent.click(screen.getByRole('radio',{name:'Berufe'}));
 await act(async()=>window.history.back());
 await screen.findByRole('dialog');
 fireEvent.click(screen.getByRole('button',{name:'Keep practising'}));
 expect(screen.getByRole('radio',{name:'Berufe'})).toBeChecked();
 fireEvent.click(screen.getByRole('button',{name:'Check answer'}));
 expect(screen.getByText('Correct')).toBeInTheDocument();
 expect(JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!).feedback.outcome).toBe('correct');
});


it("preserves an unfinished round while visiting Settings from Home", () => {
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Start practice" }));
  fireEvent.click(screen.getByRole("radio", { name: "Berufe" }));
  const snapshot = sessionStorage.getItem(TEMPORARY_KEY);
  cleanup();
  window.history.replaceState(null, "", "/#/learn/home");
  render(<GuestStarterJourney onAuth={vi.fn()} />);
  fireEvent.click(screen.getByRole("button", { name: "Settings", exact: true }));
  fireEvent.click(screen.getByRole("button", { name: "Home", exact: true }));
  expect(sessionStorage.getItem(TEMPORARY_KEY)).toBe(snapshot);
  fireEvent.click(screen.getByRole("button", { name: "Continue session" }));
  expect(screen.getByRole("radio", { name: "Berufe" })).toBeChecked();
});
