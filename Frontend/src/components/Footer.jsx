export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-10">
      <div className="max-w-7xl mx-auto px-6 py-10 grid grid-cols-3 gap-8">
        
        {/* Company */}
        <div>
          <h2 className="text-white font-semibold mb-3">Logger System</h2>
          <p className="text-sm">
            A modern logging platform for developers with real-time analytics and role-based access.
          </p>
        </div>

        {/* Links */}
        <div>
          <h2 className="text-white font-semibold mb-3">Quick Links</h2>
          <ul className="space-y-2 text-sm">
            <li><a href="#" className="hover:text-white">Docs</a></li>
            <li><a href="#" className="hover:text-white">Dashboard</a></li>
            <li><a href="#" className="hover:text-white">API</a></li>
          </ul>
        </div>

        {/* Contact */}
        <div>
          <h2 className="text-white font-semibold mb-3">Contact</h2>
          <p className="text-sm">support@logger.com</p>
          <p className="text-sm">+91 98765 43210</p>
        </div>
      </div>

      {/* Bottom */}
      <div className="border-t border-gray-700 text-center py-4 text-sm">
        © 2026 Logger System. All rights reserved.
      </div>
    </footer>
  );
}