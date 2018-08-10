import axios from 'axios';
import React from 'react'

class RaceTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            raceInfo: [{"id":1,"raceType":"Loading your","state":"a second","Candidate1":{"id":1,"name":"dem1","party":"Democrat"},"Candidate2":{"id":2,"name":"gop1","party":"Republican"}}]
        }
    }
    componentDidMount () {
    axios.get('/raceJson').then((response) => {
        const json = response.data;
        console.log(json);
        this.setState({
            raceInfo:json
        });
    })};
    render () {
        console.log(this.state.raceInfo);
        const options = this.state.raceInfo.map((item, index) => <li key={index}>{`<a href='/race/(${item.id})' className="list-group-item">${item.raceType} election in ${item.state}`}</li>);
        return (
            <div class="col-sm-4">
                <div class="list-group">
                {options}
                </div>
            </div>
        );
    }
}

export default RaceTable;
