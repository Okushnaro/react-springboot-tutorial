import React from "react";
import './Hero.css';



function importAll(r) {
    return r.keys().map(r);
  }


export function Hero(){
    const images = importAll(require.context('./images/', false, /\.(png|jpe?g|svg)$/));
    let classValue = "";
    return (
        <div className="hero-content">
            <div >
                {                    
                    images.map((img, index) => {
                        if(index === 0) {classValue = "align-flex-center"}    
                        else if(index%2 !== 0) {classValue = "align-flex-start"}
                        else if(index%2 === 0) {classValue = "align-flex-end"}
                    return <img src={img} key={index} className={classValue} alt=""></img>
                    })}
            </div>
            <p className="hero-title">Online Experiences</p>
            <p className="hero-text">Join unique interactive activities led by one-of-a-kind hosts—all without leaving home.</p>
        </div>
    )
}


