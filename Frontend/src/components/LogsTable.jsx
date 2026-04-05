export default function LogsTable({ logs }) {
  return (
    <div className="w-full bg-zinc-950 border border-zinc-800 rounded-xl shadow-2xl overflow-hidden">
      {/* Header Section */}
      <div className="px-6 py-4 border-b border-zinc-800 bg-zinc-900/50">
        <h2 className="text-sm font-semibold text-zinc-100 uppercase tracking-widest">System Logs</h2>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-zinc-800 bg-zinc-900/20">
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">Message</th>
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">Level</th>
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">Project</th>
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">Module</th>
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">Sub-Module</th>
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider">Time</th>
              <th className="px-4 py-3 text-[11px] font-bold text-zinc-500 uppercase tracking-wider text-right">Stack Trace</th>
            </tr>
          </thead>

          <tbody className="divide-y divide-zinc-900">
            {logs.map((log, i) => (
              <tr key={i} className="group hover:bg-zinc-900/40 transition-colors">
                
                {/* Message */}
                <td className="px-4 py-3 text-sm text-zinc-300 min-w-[200px]">
                  {log.message}
                </td>

                {/* Level Badge */}
                <td className="px-4 py-3">
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded border ${
                    log.level === "ERROR" ? "bg-red-500/10 text-red-400 border-red-500/20" :
                    log.level === "WARN" ? "bg-amber-500/10 text-amber-400 border-amber-500/20" :
                    log.level === "INFO" ? "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" :
                    "bg-zinc-800 text-zinc-400 border-zinc-700"
                  }`}>
                    {log.level}
                  </span>
                </td>

                {/* Project (Mono for IDs) */}
                <td className="px-4 py-3 text-xs font-mono text-zinc-500">
                  {log.projectId}
                </td>

                {/* Module */}
                <td className="px-4 py-3 text-xs font-mono text-zinc-500">
                  {log.moduleId}
                </td>

                {/* Sub-Module */}
                <td className="px-4 py-3 text-xs font-mono text-zinc-500">
                  {log.subModuleId || "—"}
                </td>

                {/* Time */}
                <td className="px-4 py-3 text-xs text-zinc-400 whitespace-nowrap">
                  {new Date(log.timestamp).toLocaleString([], { 
                    dateStyle: 'short', 
                    timeStyle: 'short',
                    hour12: false 
                  })}
                </td>

                {/* Stack Trace */}
                <td className="px-4 py-3 text-right">
                  {(log.level === "ERROR" || log.level === "FATAL") && log.stackTrace ? (
                    <button
                      className="text-[10px] font-bold text-red-400 hover:text-red-300 underline underline-offset-4 decoration-red-500/30"
                      onClick={() => alert(log.stackTrace)}
                    >
                      VIEW TRACE
                    </button>
                  ) : (
                    <span className="text-zinc-800">—</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}