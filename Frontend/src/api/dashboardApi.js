// import axios from "axios";

// const BASE = "http://localhost:8080";

// export const getLogs = (apiKey) =>
//   axios.get(`${BASE}/dashboard/logs`, {
//     headers: { Authorization: `Bearer ${apiKey}` }
//   });

// export const getSummary = (apiKey) =>
//   axios.get(`${BASE}/dashboard/summary`, {
//     headers: { Authorization: `Bearer ${apiKey}` }
//   });

// export const getSidebar = (apiKey) =>
//   axios.get(`${BASE}/dashboard/sidebar`, {
//     headers: { Authorization: `Bearer ${apiKey}` }
//   });

// export const getUser = (apiKey) =>
//   axios.get(`${BASE}/dashboard/me`, {
//     headers: { Authorization: `Bearer ${apiKey}` }
//   });

import axios from "axios";

const BASE = "http://localhost:8080";

export const getLogs = () =>
  axios.get(`${BASE}/dashboard/logs`);

export const getSummary = () =>
  axios.get(`${BASE}/dashboard/summary`);

export const getSidebar = () =>
  axios.get(`${BASE}/dashboard/sidebar`);