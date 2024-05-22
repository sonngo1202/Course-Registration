import logo from './logo.svg';
import './App.css';
import { Route, Routes } from 'react-router-dom';
import ShowProcessBar from './component/ShowProcessBar';
import Home from './component/Home';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path='/' element={<Home/>}></Route>
        <Route path='/process-bar/' element={<ShowProcessBar />}></Route>
      </Routes>
    </div>
  );
}

export default App;
