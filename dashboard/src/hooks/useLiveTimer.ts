import { useState, useEffect } from 'react';

export function useLiveTimer(lastInTime: string | null): string {
  const [elapsed, setElapsed] = useState('');

  useEffect(() => {
    if (!lastInTime) {
      setElapsed('');
      return;
    }

    const start = new Date(lastInTime).getTime();

    const tick = () => {
      const now = Date.now();
      const diffMs = now - start;
      if (diffMs < 0) { setElapsed('00:00:00'); return; }
      const totalSeconds = Math.floor(diffMs / 1000);
      const h = Math.floor(totalSeconds / 3600);
      const m = Math.floor((totalSeconds % 3600) / 60);
      const s = totalSeconds % 60;
      setElapsed(
        `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
      );
    };

    tick();
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [lastInTime]);

  return elapsed;
}
