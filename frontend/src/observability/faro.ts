import { initializeFaro, getWebInstrumentations } from '@grafana/faro-web-sdk'
import { TracingInstrumentation } from '@grafana/faro-web-tracing'

const FARO_URL = import.meta.env.VITE_FARO_URL ?? '/faro/collect'

export function initFaro() {
  if (typeof window === 'undefined') return
  if (window.location.hostname === 'localhost' && !import.meta.env.VITE_FARO_URL) {
    return
  }

  initializeFaro({
    url: FARO_URL,
    app: {
      name: 'base-frontend',
      version: import.meta.env.VITE_BUILD_HASH ?? 'dev',
      environment: import.meta.env.MODE,
    },
    instrumentations: [
      ...getWebInstrumentations({
        captureConsole: true,
        captureConsoleDisabledLevels: ['log', 'debug', 'info', 'trace'],
      }),
      new TracingInstrumentation({
        instrumentationOptions: {
          propagateTraceHeaderCorsUrls: [
            /^https?:\/\/ark-base\.site\/api(\/|$)/,
            /^https?:\/\/localhost:3000\/api(\/|$)/,
            /^\/api(\/|$)/,
          ],
        },
      }),
    ],
  })
}
