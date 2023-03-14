import logo from './logo.svg';
import './App.css';
import { Navbar } from './Navbar/Navbar';
import { Hero } from './Hero/Hero';
import { Card } from './Card/Card';
import data from './data';

function App() {

  const nums = [1, 2, 3, 4, 5];
  const names = ["alice", "bob", "charlie", "danielle"];
  const cards = data.map((cardData, index) => (<Card {...cardData} key={cardData.id}/>))
  return (
    <div className="App">
      <Navbar/>
      <main>
        <Hero/>
        <section className="card-list">
          {cards}
        </section>
        {/* <Card
          cardStatus='SOLD OUT'
          photoImage='image12.png'

          rating={5.7}
          reviewCount={100}
          country='USA'
          title="Life lessons with Katie Zaferes"
          price={136.99}
        /> */}
        
      </main>
    </div>
  );
}

export default App;
