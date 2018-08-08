import axios from 'axios';
import React from 'react'

class RaceTable extends React.Component { componentDidMount = () => {
    axios.get('/raceJson').then((response) => {
        const json = response.data;
        this.setState({
            raceid: json.race.raceid
        });
    })};
    render = () => {
        return <table>
            <tbody>
            <tr>
                <td>raceid</td>
                <td>{this.state.raceid}</td>
            </tr>
            </tbody>
        </table>
    }
}

export default RaceTable;
