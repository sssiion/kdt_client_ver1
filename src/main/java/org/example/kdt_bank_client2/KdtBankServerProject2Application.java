package org.example.kdt_bank_client2;



import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KdtBankServerProject2Application {
	public static void main(String[] args) {
		// 🔥 서버 체크 먼저 수행
		if (!isServerRunning()) {
			System.err.println("❌ 서버(localhost:8080)에 연결할 수 없습니다.");
			System.err.println("서버를 먼저 실행해주세요.");

			// GUI로도 오류 표시
			javax.swing.SwingUtilities.invokeLater(() -> {
				javax.swing.JOptionPane.showMessageDialog(null,
						"서버(localhost:8080)에 연결할 수 없습니다.\n서버를 먼저 실행해주세요.",
						"서버 연결 오류",
						javax.swing.JOptionPane.ERROR_MESSAGE);
			});

			System.exit(1);
			return;
		}

		// 서버 연결 확인 후 JavaFX 실행
		System.setProperty("java.awt.headless", "false");
		javafx.application.Application.launch(ChatClientApp.class, args);
	}

	private static boolean isServerRunning() {
		try (java.net.Socket socket = new java.net.Socket()) {
			socket.connect(new java.net.InetSocketAddress("localhost", 8080), 3000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}