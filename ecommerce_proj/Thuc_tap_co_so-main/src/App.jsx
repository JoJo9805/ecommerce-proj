import React from 'react'
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom'
import Login from './pages/public/Login'
import Header from './pages/public/Header'
import Home from './pages/public/Home'
import Cart from './pages/public/Cart'
import Shop from './pages/public/Shop'
import Product from './pages/public/Product'
import Account from './pages/public/Account'
import ShopHome from './pages/seller/ShopHome'
import {Chatbot} from './pages/public/Chatbot'

const MainLayout = () => {
  return (
    <>
      <Header />
      <Outlet />
      <Chatbot />
    </>
  )
}

const NoLayout = () => {
  return <Outlet />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<NoLayout />}>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/seller/:active_tab/:id" element={<ShopHome />} />
        </Route>

        <Route element={<MainLayout />}>
          <Route path="/home" element={<Home />} />
          <Route path="/product/:id" element={<Product />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/shop/:id" element={<Shop />} />
          <Route path="/account" element={<Navigate to="/account/profile" replace />} />
          <Route path="/account/:active_tab" element={<Account />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}