import React from 'react';
import './Header.css';

const Header = () => {
  return (
    <div className="header">
      <div className='btn-container'>
        <button className='btn'>Expenses</button>
        <button className='btn disabled'>Planning</button>
        <button className='btn'>API</button>
        <button className='btn disabled'>Logout</button>
      </div>
    </div>
  );
};

export default Header;