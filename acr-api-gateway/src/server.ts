/**
 * ACR Platform API Gateway
 * Fastify-based backend API for orchestrating agents, cases, and federated learning
 */

import Fastify from 'fastify';
import cors from '@fastify/cors';
import jwt from '@fastify/jwt';
import rateLimit from '@fastify/rate-limit';
import helmet from '@fastify/helmet';
import { logger } from '@acr/core';

// Import routes
import authRoutes from './routes/auth';
import casesRoutes from './routes/cases';
import agentsRoutes from './routes/agents';
import flRoutes from './routes/fl-submit';

// Import middleware
import { authMiddleware } from './middleware/auth';

const PORT = parseInt(process.env.PORT || '3000', 10);
const HOST = process.env.HOST || '0.0.0.0';

const server = Fastify({
  logger: {
    level: process.env.LOG_LEVEL || 'info',
    transport: {
      target: 'pino-pretty',
      options: {
        colorize: true,
      },
    },
  },
});

async function start() {
  try {
    // Security plugins
    await server.register(helmet);
    await server.register(cors, {
      origin: process.env.CORS_ORIGIN || '*',
      credentials: true,
    });

    // Rate limiting
    await server.register(rateLimit, {
      max: 100,
      timeWindow: '1 minute',
    });

    // JWT authentication
    await server.register(jwt, {
      secret: process.env.JWT_SECRET || 'your-secret-key-change-in-production',
    });

    // Register authentication decorator
    server.decorate('authenticate', authMiddleware);

    // Health check
    server.get('/health', async () => {
      return {
        status: 'healthy',
        timestamp: new Date().toISOString(),
        version: '0.8.0',
      };
    });

    // Register routes
    await server.register(authRoutes, { prefix: '/api/auth' });
    await server.register(casesRoutes, { prefix: '/api/cases' });
    await server.register(agentsRoutes, { prefix: '/api/agents' });
    await server.register(flRoutes, { prefix: '/api/fl' });

    // Start server
    await server.listen({ port: PORT, host: HOST });

    logger.info('ACR Platform API Gateway', `Server listening on ${HOST}:${PORT}`);
  } catch (err) {
    server.log.error(err);
    process.exit(1);
  }
}

// Handle shutdown gracefully
process.on('SIGTERM', async () => {
  logger.info('ACR Platform API Gateway', 'SIGTERM signal received: closing HTTP server');
  await server.close();
  process.exit(0);
});

start();

export default server;
