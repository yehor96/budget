import './App.css';

function App() {
  return (
    <div className="App">
      <h2>Welcome to Budget App</h2>
      <form action="http://localhost:8080/swagger">
        <input type="submit" value="API" />
      </form>
    </div>
  );
}

export default App;