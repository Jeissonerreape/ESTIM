import "@testing-library/jest-dom";

// Polyfill para TextEncoder/TextDecoder requerido por react-router
if (!global.TextEncoder) {
  const { TextEncoder, TextDecoder } = require("util");
  global.TextEncoder = TextEncoder;
  global.TextDecoder = TextDecoder;
}
