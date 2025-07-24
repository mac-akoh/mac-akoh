import React, { useState } from 'react';
import './SearchFilter.css';

const SearchFilter = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirmation, setPasswordConfirmation] = useState('');

  const handleSubmit = () => {
    console.log({
      firstName,
      lastName,
      email,
      password,
      passwordConfirmation,
    });
  };

  const list = [
    { firstName: 'John', lastName: 'Doe', email: 'john.doe@example.com' },
    { firstName: 'Jane', lastName: 'Doe', email: 'jane.doe@example.com' },
    { firstName: 'Peter', lastName: 'Jones', email: 'peter.jones@example.com' },
    { firstName: 'Mary', lastName: 'Smith', email: 'mary.smith@example.com' },
  ];

  const filteredList = list.filter(item => {
    return (
      item.firstName.toLowerCase().includes(firstName.toLowerCase()) &&
      item.lastName.toLowerCase().includes(lastName.toLowerCase()) &&
      item.email.toLowerCase().includes(email.toLowerCase())
    );
  });

  return (
    <div className="search-filter">
      <input
        type="text"
        placeholder="First Name"
        value={firstName}
        onChange={e => setFirstName(e.target.value)}
      />
      <input
        type="text"
        placeholder="Last Name"
        value={lastName}
        onChange={e => setLastName(e.target.value)}
      />
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={e => setEmail(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password Confirmation"
        value={passwordConfirmation}
        onChange={e => setPasswordConfirmation(e.target.value)}
      />
      <button onClick={handleSubmit}>Submit</button>
      <ul>
        {filteredList.map((item, index) => (
          <li key={index}>
            {item.firstName} {item.lastName} ({item.email})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchFilter;
