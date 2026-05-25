import React, { useState, useEffect, useRef } from "react";
import '../../styles/public/Chatbot.css';

export function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { sender: "bot", text: "Xin chào! Mình là trợ lý ảo ShopZone. Mình có thể giúp gì cho bạn hôm nay?" }
  ]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Hàm xử lý gửi tin nhắn thông qua Backend Spring Boot
  const handleSendMessage = async () => {
    if (!input.trim() || isLoading) return;

    const userMessage = { sender: "user", text: input };
    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      // Gọi tới API endpoint của Spring Boot Backend thay vì Google trực tiếp
      const response = await fetch("http://localhost:8081/api/chatbot/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        // Gửi dữ liệu đúng cấu trúc trường "message" mà ChatRequest của BE cần nhận
        body: JSON.stringify({ message: userMessage.text }),
      });

      if (!response.ok) {
        throw new Error("Lỗi phản hồi từ hệ thống Backend");
      }

      const data = await response.json();

      // Nhận thuộc tính phản hồi từ đối tượng ChatResponse của BE
      // (Giả định trường chứa câu trả lời của bạn trong ChatResponse là data.reply hoặc data.message)
      const aiReply = data.reply || data.message || "Hệ thống không phản hồi nội dung.";
      
      setMessages((prev) => [...prev, { sender: "bot", text: aiReply }]);

    } catch (error) {
      console.error("Lỗi khi kết nối qua Backend:", error);
      setMessages((prev) => [...prev, { sender: "bot", text: "Không thể kết nối đến hệ thống server. Vui lòng thử lại sau!" }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="chatbot-container">
      {!isOpen && (
        <button onClick={() => setIsOpen(true)} className="chatbot-toggle-btn">
            🤖
        </button>
      )}

      {isOpen && (
        <div className="chatbot-window">
          {/* Header */}
          <div className="chatbot-header">
            <span className="chatbot-title">🤖 Trợ lý ảo ShopZone</span>
            <button onClick={() => setIsOpen(false)} className="chatbot-close-btn">✖</button>
          </div>

          {/* Nội dung tin nhắn */}
          <div className="chatbot-messages-area">
            {messages.map((msg, index) => (
              <div 
                key={index} 
                className={`chatbot-message ${msg.sender === "user" ? "user" : "bot"}`}
              >
                {msg.text}
              </div>
            ))}
            {isLoading && (
              <div className="chatbot-loading">
                ShopZone đang suy nghĩ...
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Ô nhập tin nhắn */}
          <div className="chatbot-input-area">
            <input 
              type="text" 
              value={input} 
              onChange={(e) => setInput(e.target.value)} 
              onKeyDown={(e) => e.key === "Enter" && handleSendMessage()} 
              placeholder="Nhập câu hỏi của bạn..." 
              className="chatbot-input-field" 
            />
            <button onClick={handleSendMessage} className="chatbot-send-btn">Gửi</button>
          </div>
        </div>
      )}
    </div>
  );
}