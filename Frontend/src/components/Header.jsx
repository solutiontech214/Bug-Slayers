import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

export default function Header() {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 0);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return (
    <header 
      className={`sticky top-0 z-50 w-full transition-shadow duration-300 
      bg-zinc-950/90 backdrop-blur-md border-b 
      ${isScrolled 
        ? "border-zinc-800 shadow-[0_8px_30px_rgba(0,0,0,0.5)]" 
        : "border-zinc-900 shadow-none"
      }`}
    >
      <div className="max-w-7xl mx-auto px-6 h-16 flex justify-between items-center">
        
        {/* 🌑 Brand */}
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-8 h-8 flex items-center justify-center rounded bg-indigo-600 text-white font-bold text-xs shadow-[0_0_15px_rgba(79,70,229,0.4)]">
            LS
          </div>
          <h1 className="text-xl font-bold tracking-tight text-zinc-100">
            Logger<span className="text-zinc-500 font-normal">System</span>
          </h1>
        </Link>

        {/* 🛸 Navigation (Static Dark Pill) */}
        <nav className="hidden md:flex items-center gap-1 bg-zinc-900/80 border border-zinc-800 rounded-full px-1.5 py-1">
          {['Docs', 'Pricing', 'Blog'].map((item) => (
            <Link
              key={item}
              to={`/${item.toLowerCase()}`}
              className="px-4 py-1.5 text-xs font-medium text-zinc-400 hover:text-white transition-all rounded-full hover:bg-zinc-800"
            >
              {item}
            </Link>
          ))}
        </nav>

        {/* 💎 Actions */}
        <div className="flex items-center gap-4">
          <Link to="/login" className="text-xs font-medium text-zinc-400 hover:text-white transition-colors">
            Sign in
          </Link>
          
          <button className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold rounded-lg transition-all shadow-lg shadow-indigo-500/20 active:scale-95">
            Start Building
          </button>
        </div>
      </div>
    </header>
  );
}