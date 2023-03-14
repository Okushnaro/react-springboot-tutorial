import React from "react";
import './Card.css';
import Photo from './images/no-picture-default.jpg';

import Star from './images/Star 1.png';

export function Card({cardStatus, coverImg, stats:{rating, reviewCount},  country, title, price, location,  openSpots}){
    const defultFloatintZero = 0.0;
    let badgeText;
    if (openSpots === 0) {
        badgeText = 'SOLD OUT'
    } else if(location === 'Online'){
        badgeText = 'ONLINE';
    }
    return (
        <div className="card">
            <div className="card--photo">
                {badgeText && <div className="card--photo-status"><span>{badgeText}</span></div>}
                <img className="card--photo-image" src={(coverImg) ? `../../images/${coverImg}` : Photo} alt=""/>
            </div>
            <div className="card--info">
                <div className="card--stats">
                    <img className="card--star" src={Star} alt=""/>
                    <span className="card--rating">{(rating) ? rating.toFixed(1) : defultFloatintZero.toFixed(2)}</span>
                    <span className="card--review"> ({reviewCount ||  0}) &#x2022; {location || 'UNKNOWN'}</span>
                </div>
                
                <span className="card--text">{title || 'No title'}</span>
                
                <div className="card--cost">
                    <span className="card--price">From ${price ? price.toFixed(2) : defultFloatintZero.toFixed(2)} </span>
                    <span className="card--person">&nbsp;/ person</span>
                </div>
            </div>
        </div>
    )
}