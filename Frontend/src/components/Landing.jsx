export default function Landing() {
  return (
    <div className="min-h-screen bg-white text-black dark:bg-black dark:text-white">

      {/* 🔥 Navbar */}
      <nav className="flex justify-between items-center px-8 py-6 border-b border-gray-200 dark:border-gray-800">
        <h1 className="text-xl font-semibold">LogTrack</h1>

        <div className="flex gap-6 text-sm text-gray-600 dark:text-gray-400">
          <a href="#" className="hover:text-black dark:hover:text-white">Features</a>
          <a href="#" className="hover:text-black dark:hover:text-white">Pricing</a>
          <a href="#" className="hover:text-black dark:hover:text-white">Docs</a>
        </div>

        <button className="px-4 py-2 rounded-lg bg-black text-white dark:bg-white dark:text-black text-sm">
          Login
        </button>
      </nav>

      {/* 🔥 Hero Section */}
      <section className="text-center px-6 py-20 max-w-4xl mx-auto">
        <h1 className="text-5xl font-semibold leading-tight mb-6">
          Monitor Logs <br /> Like a Pro 🚀
        </h1>

        <p className="text-gray-600 dark:text-gray-400 text-lg mb-8">
          Track, analyze, and visualize logs from all your services in one place.
          Fast, simple, and developer-friendly.
        </p>

        <div className="flex justify-center gap-4">
          <button className="px-6 py-3 rounded-xl bg-black text-white dark:bg-white dark:text-black">
            Get Started
          </button>

          <button className="px-6 py-3 rounded-xl border border-gray-300 dark:border-gray-700">
            View Demo
          </button>
        </div>
      </section>

      {/* 🔥 Features */}
      <section className="px-8 py-16 grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
        {[
          {
            title: "Real-time Logs",
            desc: "See logs instantly as they happen across your system."
          },
          {
            title: "Powerful Dashboard",
            desc: "Visualize INFO, WARN, ERROR with beautiful charts."
          },
          {
            title: "Easy Integration",
            desc: "Plug into any backend with simple API."
          }
        ].map((f, i) => (
          <div
            key={i}
            className="p-6 rounded-2xl border border-gray-200 dark:border-gray-800 shadow-sm hover:shadow-md transition"
          >
            <h3 className="text-lg font-semibold mb-2">{f.title}</h3>
            <p className="text-gray-600 dark:text-gray-400 text-sm">
              {f.desc}
            </p>
          </div>
        ))}
      </section>

      {/* 🔥 CTA Section */}
      <section className="text-center py-20 px-6 border-t border-gray-200 dark:border-gray-800">
        <h2 className="text-3xl font-semibold mb-4">
          Start Monitoring Today
        </h2>

        <p className="text-gray-600 dark:text-gray-400 mb-6">
          No setup headaches. Just plug and track.
        </p>

        <button className="px-8 py-3 rounded-xl bg-black text-white dark:bg-white dark:text-black">
          Get Started Free
        </button>
      </section>

      {/* 🔥 Footer */}
      <footer className="text-center text-sm text-gray-500 dark:text-gray-400 py-6 border-t border-gray-200 dark:border-gray-800">
        © 2026 LogTrack. Built for developers.
      </footer>
    </div>
  );
}