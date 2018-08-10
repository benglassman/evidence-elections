import Bootstrap from 'bootstrap'
import React from 'react';
import ReactDOM from 'react-dom';
import TopNavbar from './TopNavbar.jsx';
import RaceTable from './RaceTable.jsx';

ReactDOM.render(<TopNavbar />, document.getElementById('navbarView'));
ReactDOM.render(<RaceTable />, document.getElementById('raceTableView'));
