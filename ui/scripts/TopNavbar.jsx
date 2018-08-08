import React from 'react'

class TopNavbar extends React.Component {
    constructor(props) {
        super(props);
    }
    render = () => {
        return <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <div className="container">
                <a className="navbar-brand" href="#">Evidence Elections</a>
                <button className="navbar-toggler" type="button" data-toggle="collapse"
                        data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                        aria-expanded="false"
                        aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav mr-auto">
                        <li className="nav-item active">
                            <a className="nav-link" href="/">Home <span className="sr-only">(current)</span></a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="/user">Profile</a>
                        </li>
                        <li className="nav-item dropdown">
                            <a className="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                               data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                My Races
                            </a>
                            <div className="dropdown-menu" aria-labelledby="navbarDropdown">
                                <a className="dropdown-item" href="/race/1">Senate</a>
                                <a className="dropdown-item" href="/race/2">House</a>
                                <div className="dropdown-divider"></div>
                                <a className="dropdown-item" href="#">All Races</a>
                            </div>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link disabled" href="#">Disabled</a>
                        </li>
                    </ul>
                    <div>
                        <div className="login-box auth0-box before">
                            <a className="btn btn-primary" href="/login">SignIn</a>
                        </div>
                    </div>
                </div>
                </div>
            </nav>
        </div>
    }
}

export default TopNavbar;