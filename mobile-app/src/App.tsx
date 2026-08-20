import {
  HashRouter,
  Navigate,
  Outlet,
  Route,
  Routes,
  useLocation,
} from 'react-router-dom';
import { BottomNav } from './components/BottomNav';
import { getSession } from './lib/session';
import { HomeScreen } from './screens/HomeScreen';
import { LimitsScreen } from './screens/LimitsScreen';
import { LoginScreen } from './screens/LoginScreen';
import { PinScreen } from './screens/PinScreen';
import { RequestCardScreen } from './screens/RequestCardScreen';
import { StatusScreen } from './screens/StatusScreen';

function RequireAuth() {
  const session = getSession();
  const location = useLocation();
  if (!session?.token) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return (
    <div className="app-shell">
      <Outlet />
      <BottomNav />
    </div>
  );
}

export default function App() {
  return (
    <HashRouter>
      <div className="device-frame">
        <Routes>
          <Route path="/login" element={<LoginScreen />} />
          <Route element={<RequireAuth />}>
            <Route path="/" element={<Navigate to="/home" replace />} />
            <Route path="/home" element={<HomeScreen />} />
            <Route path="/request" element={<RequestCardScreen />} />
            <Route path="/status" element={<StatusScreen />} />
            <Route path="/pin" element={<PinScreen />} />
            <Route path="/limits" element={<LimitsScreen />} />
          </Route>
          <Route path="*" element={<Navigate to="/home" replace />} />
        </Routes>
      </div>
    </HashRouter>
  );
}
