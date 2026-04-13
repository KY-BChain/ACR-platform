/**
 * Authentication Routes
 * ERC-3643 identity verification and JWT token issuance
 */

import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';

const authRoutes: FastifyPluginAsync = async (fastify) => {
  // Login with wallet signature
  fastify.post('/login', {
    handler: async (request, reply) => {
      const loginSchema = z.object({
        address: z.string(),
        signature: z.string(),
        message: z.string(),
      });

      const { address, signature, message } = loginSchema.parse(request.body);

      // TODO: Verify signature
      // TODO: Check ERC-3643 identity on blockchain
      // TODO: Generate JWT token

      const token = fastify.jwt.sign({
        userId: address,
        address,
        did: `did:ethr:${address}`,
      });

      return {
        success: true,
        data: {
          token,
          user: {
            address,
            did: `did:ethr:${address}`,
          },
        },
      };
    },
  });

  // Verify token
  fastify.get('/verify', {
    onRequest: [fastify.authenticate],
    handler: async (request, reply) => {
      return {
        success: true,
        data: {
          user: request.user,
        },
      };
    },
  });

  // Logout
  fastify.post('/logout', {
    onRequest: [fastify.authenticate],
    handler: async (request, reply) => {
      // TODO: Invalidate token (add to blacklist in Redis)

      return {
        success: true,
        message: 'Logged out successfully',
      };
    },
  });
};

export default authRoutes;
