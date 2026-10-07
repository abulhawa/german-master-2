import "./foundation.css";
import type { ButtonHTMLAttributes, ReactNode } from "react";
export function FoundationButton(
  props: ButtonHTMLAttributes<HTMLButtonElement>,
) {
  return <button {...props} className={`gm-button ${props.className ?? ""}`} />;
}
export function PracticeCard({ children }: { children: ReactNode }) {
  return <section className="gm-card">{children}</section>;
}
