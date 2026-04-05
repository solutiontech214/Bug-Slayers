export default function StatsCards({ summary }) {
  const card = (title, value, color) => (
    <div className={`p-4 rounded-xl ${color} flex-1 shadow`}>
      <h3 className="text-sm">{title}</h3>
      <p className="text-2xl font-bold">{value || 0}</p>
    </div>
  );

  return (
    <div className="flex gap-4">
      {card("INFO", summary.INFO, "bg-green-500")}
      {card("WARN", summary.WARN, "bg-yellow-500")}
      {card("ERROR", summary.ERROR, "bg-red-500")}
      {card("FATAL", summary.FATAL, "bg-purple-500")}
    </div>
  );
}