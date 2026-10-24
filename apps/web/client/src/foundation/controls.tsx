import "./foundation.css";
import type { ButtonHTMLAttributes, ReactNode } from "react";
export function FoundationButton(
  props: ButtonHTMLAttributes<HTMLButtonElement>,
) {
  return <button {...props} className={`gm-button ${props.className ?? ""}`} />;
}
export function PracticeCard({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <section className={`gm-card ${className}`}>{children}</section>;
}
