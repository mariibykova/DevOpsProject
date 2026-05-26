import '@testing-library/jest-dom';

// Node.js 25+ has a built-in localStorage that lacks clear().
// Override with a full Storage mock so jsdom tests work correctly.
const storageMock = (() => {
  let store = {};
  return {
    getItem: (key) => (key in store ? store[key] : null),
    setItem: (key, value) => { store[key] = String(value); },
    removeItem: (key) => { delete store[key]; },
    clear: () => { store = {}; },
    get length() { return Object.keys(store).length; },
    key: (index) => Object.keys(store)[index] ?? null,
  };
})();

Object.defineProperty(globalThis, 'localStorage', { value: storageMock, writable: true });
