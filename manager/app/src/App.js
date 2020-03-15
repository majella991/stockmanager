import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
  state = {
    isLoading: true,
    products: []
  };

  async componentDidMount() {
    const response = await fetch('/api/stock/');
    const body = await response.json();
    this.setState({ products: body, isLoading: false });
  }

  render() {
    const {products, isLoading} = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }

    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <div className="App-intro">
           <h2>Stock</h2>
            <table>
            <thead>
            <tr>
              <th>Product</th>
              <th>Available Items</th>
            </tr>
            </thead>
            <tbody>
            {products.map(product =>
            <tr key={product.id}>
             <td>{product.name}</td>
             <td>{product.quantity}</td>
            </tr>
          )}
            </tbody>
          </table>
            
          </div>
        </header>
      </div>
    );
  }
}

export default App;