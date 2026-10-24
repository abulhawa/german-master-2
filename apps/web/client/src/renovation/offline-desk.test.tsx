import { afterEach, expect, it, vi } from 'vitest';
import { act, cleanup, render, screen } from '@testing-library/react';
import { OfflineDesk } from './offline-desk';
import type { WebReserve } from './reserve';
import type { LearnerApi } from './api';

afterEach(() => {
  cleanup();
  vi.useRealTimers();
  vi.unstubAllGlobals();
});

it('refreshes downloaded-session availability when a pack becomes valid and expires', async () => {
  const now = Date.parse('2026-10-24T08:53:20.000Z');
  const issuedAt = now + 5_000;
  const expiresAt = now + 10_000;
  vi.useFakeTimers({ toFake: ['Date', 'setTimeout', 'clearTimeout'] });
  vi.setSystemTime(now);

  const pack = {
    issuedAt: new Date(issuedAt).toISOString(),
    expiresAt: new Date(expiresAt).toISOString(),
  };
  const status = vi.fn(async (at: Date) => ({
    reserve: { pending: null, pack },
    available: at.getTime() >= issuedAt && at.getTime() < expiresAt ? 2 : 0,
  }));
  const db = {
    status,
    table: () => ({ toArray: async () => [] }),
  } as unknown as WebReserve;

  render(<OfflineDesk api={{} as LearnerApi} locale="en" deviceId="device" questionCount={5}
    blocked={false} db={db} />);
  await act(async () => { await Promise.resolve(); await Promise.resolve(); });

  expect(screen.getByText('Sessions available to start: 0')).toBeInTheDocument();
  expect(screen.getByText('These sessions are not available to start yet.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Start downloaded practice' })).toBeDisabled();
  expect(status).toHaveBeenCalledTimes(1);

  await act(async () => { await vi.advanceTimersByTimeAsync(5_001); });
  expect(status).toHaveBeenCalledTimes(2);
  expect(screen.getByText('Sessions available to start: 2')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Start downloaded practice' })).toBeEnabled();

  await act(async () => { await vi.advanceTimersByTimeAsync(5_000); });
  expect(status).toHaveBeenCalledTimes(3);
  expect(screen.getByText('Sessions available to start: 0')).toBeInTheDocument();
  expect(screen.getByText('Download expired or consumed. Started sessions remain saved.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Start downloaded practice' })).toBeDisabled();
});

it('rechecks availability when the initial status arrives after issuance', async () => {
  const now = Date.parse('2026-10-24T08:53:20.000Z');
  const issuedAt = now + 5_000;
  const expiresAt = now + 10_000;
  vi.useFakeTimers({ toFake: ['Date', 'setTimeout', 'clearTimeout'] });
  vi.setSystemTime(now);

  const pack = {
    issuedAt: new Date(issuedAt).toISOString(),
    expiresAt: new Date(expiresAt).toISOString(),
  };
  type Status = { reserve: { pending: null; pack: typeof pack }; available: number };
  let resolveInitial!: (value: Status) => void;
  const initial = new Promise<Status>(resolve => { resolveInitial = resolve; });
  let calls = 0;
  const status = vi.fn((at: Date): Promise<Status> => {
    if (calls++ === 0) return initial;
    return Promise.resolve({ reserve: { pending: null, pack },
      available: at.getTime() >= issuedAt && at.getTime() < expiresAt ? 2 : 0 });
  });
  const db = {
    status,
    table: () => ({ toArray: async () => [] }),
  } as unknown as WebReserve;

  render(<OfflineDesk api={{} as LearnerApi} locale="en" deviceId="device" questionCount={5}
    blocked={false} db={db} />);
  expect(status).toHaveBeenCalledTimes(1);
  await act(async () => { await vi.advanceTimersByTimeAsync(5_001); });
  await act(async () => {
    resolveInitial({ reserve: { pending: null, pack }, available: 0 });
    await Promise.resolve();
    await Promise.resolve();
    await Promise.resolve();
  });

  expect(status).toHaveBeenCalledTimes(2);
  expect(status.mock.calls[1][0].getTime()).toBeGreaterThanOrEqual(issuedAt);
  expect(screen.getByText('Sessions available to start: 2')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Start downloaded practice' })).toBeEnabled();
});
