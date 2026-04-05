import { useEffect, useState } from "react";
import { getLogs, getSummary, getSidebar } from "../api/dashboardApi";

import Sidebar from "../components/Sidebar";
import StatsCards from "../components/StatsCards";
import LogsTable from "../components/LogsTable";
import Chart from "../components/Chart";
import AISummary from "../components/AISummary";
export default function Dashboard() {
  const [logs, setLogs] = useState([]);
  const [filteredLogs, setFilteredLogs] = useState([]);
  const [summary, setSummary] = useState({});
  const [sidebar, setSidebar] = useState({});
     const [view, setView] = useState("logs"); 
  useEffect(() => {
    getLogs().then(res => {
      setLogs(res.data);
      setFilteredLogs(res.data);
    });

    getSummary().then(res => setSummary(res.data));
    console.log("Summary:", summary); // Debug log for summary data
    getSidebar().then(res => setSidebar(res.data));
  }, []);

  // 🔥 FILTER HANDLER
  const handleFilter = (filter) => {
   
   
   
   if (filter.type === "CHART") {
    setView("chart");
    return;
  }

  if (filter.type === "AI") {
    setView("ai");
    return;
  }

  // default = logs view
  setView("logs");
    let result = logs;

    if (filter.level) {
      result = result.filter(l => l.level === filter.level);
    }

   if (filter.search) {
  const query = filter.search.toLowerCase();

  result = result.filter(log =>
    (log.message && log.message.toLowerCase().includes(query)) ||
    (log.moduleId && log.moduleId.toLowerCase().includes(query)) ||
    (log.subModuleId && log.subModuleId.toLowerCase().includes(query)) ||
    (log.projectId && log.projectId.toLowerCase().includes(query)) ||
    (log.level && log.level.toLowerCase().includes(query)) ||
    (log.stackTrace && log.stackTrace.toLowerCase().includes(query))
  );
}

    if (filter.manager) {
      result = result.filter(l => l.projectId === filter.manager);
    }

    if (filter.developer) {
      result = result.filter(l => l.moduleId === filter.developer);
    }

    setFilteredLogs(result);
  };

  return (
    <div className="flex bg-gray-950 text-white min-h-screen">
      
      <Sidebar data={sidebar} onFilterChange={handleFilter} />

      <div className="flex-1 p-6">
        
        <StatsCards summary={summary} />

       <div className="mt-6">

  {/* 🧾 Logs */}
  {view === "logs" && <LogsTable logs={filteredLogs} />}

  {/* 📊 Charts */}
  {view === "chart" && <Chart data={summary} />}

  {/* 🤖 AI */}
  {view === "ai" && <AISummary logs={logs} />}

</div>

  
      </div>
    </div>
  );
}