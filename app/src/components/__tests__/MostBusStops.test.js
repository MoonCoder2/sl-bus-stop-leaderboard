import { render, screen, cleanup } from '@testing-library/react';
import MostBusStops from '../MostBusStops';


window.matchMedia = window.matchMedia || function() {
  return {
      matches: false,
      addListener: function() {},
      removeListener: function() {}
  };
};

afterEach(() => {
  cleanup();
});

test('Testing that it renders', () => {
  render(<MostBusStops />);
  expect(screen.getByText('Refresh')).toBeInTheDocument();
});
