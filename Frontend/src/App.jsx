import Header from "./components/Header";
import Footer from "./components/Footer";
import Panel from "./pages/Panel";
import Docs from "./pages/Docs";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
function App() {
  return (
    <Router>
      <Header />
      <main className="min-h-screen">
        <Routes>
          <Route path="/" element={<Panel />} />
          <Route path="/docs" element={<Docs />} />
          {/* Future routes can be added here */}
        </Routes>
      </main>
      <Footer />
    </Router>
  );
}

export default App;