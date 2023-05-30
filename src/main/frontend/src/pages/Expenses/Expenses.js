import React from 'react';
import Header from '../../components/Header/Header'
import PageTitle from '../../components/PageTitle/PageTitle';

const PAGE_NAME = "Expenses";

function Expenses() {
  return (
    <div>
        <Header selected={PAGE_NAME}/>
        <PageTitle pageName={PAGE_NAME}/>
    </div>
  );
}

export default Expenses;