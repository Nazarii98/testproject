import React from "react";
import { useState } from "react";
import {
  NativeModules,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

export default function App() {
  const [login, setLogin] = useState("nazarii.chepil@kevychsolutions.com");
  const [password, setPassword] = useState("Password123!");
  const [token, setToken] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    try {
      const response = await fetch(
        "https://stageapi.voicenotes.com/api/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            email: login,
            password: password,
          }),
        }
      );
      if (response.ok) {
        const data = await response.json();
        setToken(data.authorisation.token);
        NativeModules.TokenBridge.sendTokenToWatch(data.authorisation.token);
        setError("");
      } else {
        setError("Error during login. Please try again.");
      }
    } catch (error) {
      console.error("Error during login:", error);
      setError("Error during login. Please try again.");
    }
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Enter your login"
        placeholderTextColor="#aaa"
        value={login}
        onChangeText={(text) => setLogin(text)}
      />
      <TextInput
        style={styles.input}
        placeholder="Enter your password"
        placeholderTextColor="#aaa"
        secureTextEntry={true}
        value={password}
        onChangeText={(text) => setPassword(text)}
      />
      <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
        <Text style={styles.buttonText}>Login</Text>
      </TouchableOpacity>
      {error !== "" && <Text style={styles.errorText}>{error}</Text>}
      {token !== "" && (
        <View style={styles.tokenContainer}>
          <Text style={styles.tokenText}>Token:</Text>
          <Text style={styles.token}>{token}</Text>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    paddingHorizontal: 20,
  },
  input: {
    width: "100%",
    height: 50,
    backgroundColor: "#eee",
    borderRadius: 10,
    paddingHorizontal: 15,
    marginBottom: 20,
  },
  loginButton: {
    width: "100%",
    height: 50,
    backgroundColor: "#007bff",
    borderRadius: 10,
    justifyContent: "center",
    alignItems: "center",
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "bold",
  },
  tokenContainer: {
    marginTop: 20,
    alignItems: "center",
  },
  tokenText: {
    fontSize: 18,
    fontWeight: "bold",
  },
  token: {
    fontSize: 16,
    marginTop: 5,
    paddingHorizontal: 10,
    paddingVertical: 5,
    backgroundColor: "#f0f0f0",
    borderRadius: 5,
  },
  errorText: {
    color: "red",
    fontSize: 16,
    marginTop: 10,
  },
});
