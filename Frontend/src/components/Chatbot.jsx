import React, { useState } from 'react';
import axios from '../util/axiosConfig';

const Chatbot = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        { text: "Hello! How can I help you with your finances today?", sender: "bot" }
    ]);
    const [input, setInput] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const toggleChatbot = () => setIsOpen(!isOpen);

    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (input.trim() === '' || isLoading) return;

        const userMessage = { text: input, sender: 'user' };
        setMessages((prevMessages) => [...prevMessages, userMessage]);
        const currentInput = input;
        setInput('');
        setIsLoading(true);

        try {
            // This makes the REST API call to your backend
            const response = await axios.post('/ai/chat', { prompt: currentInput });
            
            // The ChatResponse DTO has a 'response' field
            const botMessage = { text: response.data.response, sender: 'bot' };
            setMessages((prevMessages) => [...prevMessages, botMessage]);
        } catch (error) {
            const errorMessage = { text: "Sorry, something went wrong. Please try again.", sender: 'bot' };
            setMessages((prevMessages) => [...prevMessages, errorMessage]);
            console.error("Error fetching AI response:", error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            {/* Chatbot Toggler Button */}
            <button
                onClick={toggleChatbot}
                className="fixed bottom-6 right-6 w-16 h-16 bg-purple-900 rounded-full text-white flex items-center justify-center text-3xl shadow-lg hover:bg-purple-700 transition-all z-50"
            >
                💬
            </button>

            {/* Chatbot Window */}
            {isOpen && (
                <div className="fixed bottom-24 right-6 w-96 h-[500px] bg-white rounded-2xl shadow-2xl flex flex-col z-50">
                    {/* Header */}
                    <div className="bg-purple-900 text-white p-4 rounded-t-2xl flex justify-between items-center">
                        <h2 className="font-bold text-lg">Finance Bot</h2>
                        <button onClick={toggleChatbot} className="text-2xl">&times;</button>
                    </div>

                    {/* Messages */}
                    <div className="flex-1 p-4 overflow-y-auto space-y-4">
                        {messages.map((msg, index) => (
                            <div key={index} className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
                                <div className={`max-w-xs lg:max-w-md p-3 rounded-lg ${msg.sender === 'user' ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-800'}`}>
                                    {msg.text}
                                </div>
                            </div>
                        ))}
                        {isLoading && (
                            <div className="flex justify-start">
                                <div className="bg-gray-200 text-gray-800 p-3 rounded-lg">
                                    <div className="flex items-center space-x-1">
                                        <span className="w-2 h-2 bg-gray-500 rounded-full animate-bounce"></span>
                                        <span className="w-2 h-2 bg-gray-500 rounded-full animate-bounce delay-75"></span>
                                        <span className="w-2 h-2 bg-gray-500 rounded-full animate-bounce delay-150"></span>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Input */}
                    <form className="p-4 border-t border-gray-200" onSubmit={handleSendMessage}>
                        <div className="flex items-center space-x-2">
                            <input
                                type="text"
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                placeholder="Ask a question..."
                                disabled={isLoading}
                                className="w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-purple-500"
                            />
                            <button
                                type="submit"
                                disabled={isLoading}
                                className="bg-purple-800 text-white font-bold py-2 px-4 rounded-lg hover:bg-purple-600 disabled:bg-purple-300"
                            >
                                Send
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </>
    );
};

export default Chatbot;