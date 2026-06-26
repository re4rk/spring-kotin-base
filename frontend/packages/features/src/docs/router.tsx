import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { DocsLayout } from './layouts/DocsLayout.tsx'
import { TypographyPage } from './pages/TypographyPage.tsx'
import { ColorsPage } from './pages/ColorsPage.tsx'
import { ButtonPage } from './pages/ButtonPage.tsx'
import { FormPage } from './pages/FormPage.tsx'
import { FeedbackPage } from './pages/FeedbackPage.tsx'
import { OverlayPage } from './pages/OverlayPage.tsx'
import { NavigationPage } from './pages/NavigationPage.tsx'

export function DocsRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<DocsLayout />}>
          <Route index element={<Navigate to="/typography" replace />} />
          <Route path="typography" element={<TypographyPage />} />
          <Route path="colors" element={<ColorsPage />} />
          <Route path="buttons" element={<ButtonPage />} />
          <Route path="form" element={<FormPage />} />
          <Route path="feedback" element={<FeedbackPage />} />
          <Route path="overlay" element={<OverlayPage />} />
          <Route path="navigation" element={<NavigationPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
