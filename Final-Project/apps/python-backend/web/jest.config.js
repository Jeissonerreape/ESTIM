export default {
  testEnvironment: "jsdom",
  setupFilesAfterEnv: ["<rootDir>/src/setupTests.js"],
  moduleNameMapper: {
    "\\.(css|less|scss|sass)$": "identity-obj-proxy"
  },
  transform: {
    "^.+\\.(js|jsx)$": "babel-jest"
  },
  transformIgnorePatterns: [
    "node_modules/(?!(react-router-dom|react-router)/)"
  ],
  testMatch: [
    "**/__test__/**/(*.)(test|spec).(js|jsx)"
  ],
  testEnvironmentOptions: {
    customExportConditions: [""],
    pretendToBeVisual: true
  }
};
