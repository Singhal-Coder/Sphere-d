import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import App from './App.jsx'
import { AuthProvider } from './context/AuthContext.jsx'
import { LookupsProvider } from './context/LookupsContext.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <LookupsProvider>
          <App />
        </LookupsProvider>
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>,
)
