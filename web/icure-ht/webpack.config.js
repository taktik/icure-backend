/* webpack.config.js */

var HtmlWebpackPlugin = require('html-webpack-plugin');
var CopyWebpackPlugin = require('copy-webpack-plugin');
// var HtmlIncluderWebpackPlugin = require('html-includer-webpack-plugin').default;
var Clean = require('clean-webpack-plugin');
var path = require('path');
console.log(path.resolve(__dirname))
module.exports = {
    // Tell Webpack which file kicks off our app.
    entry: path.resolve(__dirname, 'app/src/ht-app.html'),
    // Tell Weback to output our bundle to ./dist/bundle.js
    output: {
	    filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    },
    // Tell Webpack which directories to look in to resolve import statements.
    // Normally Webpack will look in node_modules by default but since we’re overriding
    // the property we’ll need to tell it to look there in addition to the
    // bower_components folder.
    resolve: {
        modules: [
            path.resolve(__dirname,  'app/bower_components'),
            path.resolve(__dirname,  'node_modules'),
        ],
        extensions: ['.ts', '.tsx', '.js', '.jsx', '.html']
    },
	devtool: 'inline-source-map',
	node: {
    	fs: 'empty'
	},
	module: {
        rules: [
	        {
		        test: /\.html$/,
		        use: [
			        {
				        loader: 'babel-loader',

				        options: {
				        	/*presets: ['es2015'],*/
					        plugins: ['babel-plugin-lodash','syntax-dynamic-import']
				        }
			        },
			        {
				        loader: 'polymer-webpack-loader'
			        }
		        ]
	        },
            {
                // If you see a file that ends in .js, just send it to the babel-loader.
                test: /\.js$/,
	            use: [{ loader: 'babel-loader', options: { /*presets: ['es2015']*/ }}],
                exclude: /(node_modules|bower_components)/
            },
            {
                test: /\.ts$/,
                use: 'ts-loader',
            },
	        {
		        test: /\.(gif|png|jpe?g|svg)$/i,
		        use: [
			        'file-loader',
			        {
				        loader: 'image-webpack-loader',
				        options: {
					        bypassOnDebug: true,
				        },
			        },
		        ],
	        },
        ]
    },
	mode: 'development',
    plugins: [
        // This plugin will generate an index.html file for us that can be used
        // by the Webpack dev server. We can give it a template file (written in EJS)
        // and it will handle injecting our bundle for us.
        new HtmlWebpackPlugin({
            inject: false,
	        debug: true,
            template: path.resolve(__dirname, 'app/index.ejs')
        }),
        // This plugin will copy files over to ‘./dist’ without transforming them.
        // That's important because the custom-elements-es5-adapter.js MUST
        // remain in ES2015. We’ll talk about this a bit later :)
        new CopyWebpackPlugin([{
            from: path.resolve(__dirname, 'app/bower_components/webcomponentsjs/*.js'),
            to: 'bower_components/webcomponentsjs/[name].[ext]'
        }]),
        new Clean(['build']),
    ],
	devServer: {
		contentBase: path.join(__dirname,'dist'),
		compress: true,
		overlay: true,
		port: 9000,
		proxy: {
			'/rest/v1': {
				target: /*'https://service.icure.cloud'*/'http://127.0.0.1:16043',
				changeOrigin: true
			}
		}
	},
};
