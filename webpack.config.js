var webpack = require('webpack');
module.exports = {
    entry: './ui/entry.js',
    output: {path: __dirname + '/public/compiled', filename: 'bundle.js'},
    module: {
        rules: [
            {
                test: /\.jsx?$/, loader: 'babel-loader',
                include: /ui/, query: {presets: ['babel-preset-env','babel-preset-stage-0','react']},
                exclude: /node_modules/
            }
        ]
    }
};