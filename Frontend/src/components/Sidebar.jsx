import { useState } from "react";

export default function Sidebar({ data, onFilterChange }) {
  const [active, setActive] = useState("");

  const handleClick = (item, filter) => {
    setActive(item);
    onFilterChange(filter);
  };

  const menuItems = ["INFO", "WARN", "ERROR", "FATAL"];

  return (
    <div className="w-64 bg-gray-900 text-gray-300 h-screen p-5 border-r border-gray-800">

      {/* 🔍 Search */}
      <input
        type="text"
        placeholder="Search logs..."
        className="w-full mb-5 px-3 py-2 rounded-lg bg-gray-800 text-white outline-none"
        onChange={(e) => handleClick("search", { search: e.target.value })}
      />

      {/* 🔥 Levels */}
      <h3 className="text-gray-400 text-xs mb-2">LOG LEVELS</h3>
      {menuItems.map((item) => (
        <div
          key={item}
          onClick={() => handleClick(item, { level: item })}
          className={`cursor-pointer px-3 py-2 rounded-lg transition 
            ${active === item ? "bg-blue-600 text-white" : "hover:bg-gray-800"}`}
        >
          {item}
        </div>
      ))}

      {/* 🤖 Tools */}
      <h3 className="text-gray-400 text-xs mt-5 mb-2">TOOLS</h3>

      <div
        onClick={() => handleClick("AI", { type: "AI" })}
        className={`px-3 py-2 rounded-lg cursor-pointer 
          ${active === "AI" ? "bg-blue-600 text-white" : "hover:bg-gray-800"}`}
      >
        🤖 AI Summarize
      </div>

      <div
        onClick={() => handleClick("CHART", { type: "CHART" })}
        className={`px-3 py-2 rounded-lg cursor-pointer 
          ${active === "CHART" ? "bg-blue-600 text-white" : "hover:bg-gray-800"}`}
      >
        📊 Charts
      </div>

      {/* 👥 Managers */}
      {data.role === "ADMIN" && (
        <>
          <h3 className="text-gray-400 text-xs mt-5 mb-2">TEAM MANAGERS</h3>
          {data.managers?.map((m, i) => (
            <div
              key={i}
              onClick={() => handleClick(m, { manager: m })}
              className={`px-3 py-2 rounded-lg cursor-pointer 
                ${active === m ? "bg-blue-600 text-white" : "hover:bg-gray-800"}`}
            >
              👤 {m}
            </div>
          ))}
        </>
      )}

      {/* 👨‍💻 Developers */}
      {(data.role === "ADMIN" || data.role === "MANAGER") && (
        <>
          <h3 className="text-gray-400 text-xs mt-5 mb-2">DEVELOPERS</h3>
          {data.developers?.map((d, i) => (
            <div
              key={i}
              onClick={() => handleClick(d, { developer: d })}
              className={`px-3 py-2 rounded-lg cursor-pointer 
                ${active === d ? "bg-blue-600 text-white" : "hover:bg-gray-800"}`}
            >
              💻 {d}
            </div>
          ))}
        </>
      )}
    </div>
  );
}