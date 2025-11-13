/**
 * Login Component
 * ERC-4337 gasless wallet connection with ERC-3643 identity verification
 */

import React, { useState } from 'react';
import { useConnect, useAccount } from 'wagmi';

export const Login: React.FC = () => {
  const { connect, connectors, isLoading } = useConnect();
  const { address, isConnected } = useAccount();
  const [isVerifying, setIsVerifying] = useState(false);

  const handleConnect = async (connector: any) => {
    try {
      await connect({ connector });
      // After connection, verify ERC-3643 identity
      setIsVerifying(true);
      // TODO: Call API to verify identity on blockchain
      setIsVerifying(false);
    } catch (error) {
      console.error('Connection error:', error);
    }
  };

  if (isConnected) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
        <div className="bg-white p-8 rounded-lg shadow-md">
          <h2 className="text-2xl font-bold text-green-600 mb-4">Connected</h2>
          <p className="text-gray-700">Wallet: {address}</p>
          {isVerifying && (
            <p className="text-blue-600 mt-4">Verifying identity...</p>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">ACR Platform</h1>
        <p className="text-gray-600 mb-6">Cervical Cancer Risk Assessment</p>

        <h2 className="text-xl font-semibold mb-4">Connect Your Wallet</h2>

        <div className="space-y-3">
          {connectors.map((connector) => (
            <button
              key={connector.id}
              onClick={() => handleConnect(connector)}
              disabled={isLoading}
              className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400"
            >
              {isLoading ? 'Connecting...' : `Connect with ${connector.name}`}
            </button>
          ))}
        </div>

        <p className="text-sm text-gray-500 mt-6 text-center">
          Gasless transactions via ERC-4337
        </p>
      </div>
    </div>
  );
};

export default Login;
