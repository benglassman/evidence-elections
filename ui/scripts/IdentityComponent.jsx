import React from 'react'

class IdentityComponent extends React.Component {
    constructor(props) {
        super(props);
    }
    render = () => {
        return <div>
            <div className="login-box auth0-box before">
                <h3>Click to SignIn!</h3>
                <a className="btn btn-primary btn-lg btn-block" href="/login">SignIn</a>
            </div>
        </div>
    }
}

export default IdentityComponent;