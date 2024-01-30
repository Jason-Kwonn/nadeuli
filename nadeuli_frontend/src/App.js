import Router from "./Router";
import { createGlobalStyle, ThemeProvider } from "styled-components";
import { defaultTheme } from "./shared/theme";
import reset from "./shared/reset.css";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
// import { carrotLoginStatus, getCarrotUserInfo } from "./redux/modules/user";
import { getToken } from "./shared/localStorage";
import { QueryClient, QueryClientProvider } from "react-query";

function App() {
  const dispatch = useDispatch();
  const queryClient = new QueryClient();

  // useEffect(() => {
  //   const token = getToken();

  //   if (token) { // 토큰이 있는지 체크
  //     dispatch(carrotLoginStatus(true));
  //     dispatch(getCarrotUserInfo());
  //   } else {
  //     dispatch(carrotLoginStatus(false));
  //   }
  // }, [dispatch]);

  return (
    <QueryClientProvider client={queryClient}>
    <ThemeProvider theme={defaultTheme}>
      <GlobalStyle />
      <Router />
    </ThemeProvider>
    </QueryClientProvider>
  );
}

const GlobalStyle = createGlobalStyle`
${reset}; // Reset CSS

body, button, input, textarea {
  color: #444444;
  font-family: ${(props) => props.theme.fontFamily.default}, sans-serif;
}

a {
  text-decoration: none;
  color: inherit;
}
`;

export default App;
