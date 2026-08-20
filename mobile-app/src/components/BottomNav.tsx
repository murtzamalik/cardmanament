import { Link, useLocation } from 'react-router-dom';

const items = [
  { to: '/home', label: 'Home', icon: '⌂' },
  { to: '/request', label: 'Request', icon: '+' },
  { to: '/status', label: 'Status', icon: '◎' },
  { to: '/pin', label: 'PIN', icon: '✱' },
  { to: '/limits', label: 'Limits', icon: '▭' },
];

export function BottomNav() {
  const { pathname } = useLocation();
  return (
    <nav className="bottom-nav" aria-label="Main">
      {items.map((item) => {
        const active = pathname === item.to;
        return (
          <Link
            key={item.to}
            to={item.to}
            className={`nav-item${active ? ' active' : ''}`}
          >
            <span className="nav-icon">{item.icon}</span>
            <span>{item.label}</span>
          </Link>
        );
      })}
    </nav>
  );
}
