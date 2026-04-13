/**
 * JWT Authentication Middleware
 */

import { FastifyRequest, FastifyReply, FastifyInstance } from 'fastify';

export async function authMiddleware(
  request: FastifyRequest,
  reply: FastifyReply
) {
  try {
    await request.jwtVerify();
  } catch (err) {
    reply.status(401).send({
      success: false,
      error: {
        code: 'UNAUTHORIZED',
        message: 'Invalid or expired token',
      },
    });
  }
}

// Extend Fastify types
declare module 'fastify' {
  interface FastifyInstance {
    authenticate: typeof authMiddleware;
  }
  interface FastifyRequest {
    user?: {
      userId: string;
      address: string;
      did: string;
    };
  }
}

export default authMiddleware;
