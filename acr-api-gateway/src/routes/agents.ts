/**
 * Agents API Routes
 * Manage and monitor agent system
 */

import { FastifyPluginAsync } from 'fastify';

const agentsRoutes: FastifyPluginAsync = async (fastify) => {
  // Get all agents status
  fastify.get('/', {
    onRequest: [fastify.authenticate],
    handler: async (request, reply) => {
      // TODO: Query agent status from Redis

      return {
        success: true,
        data: [
          {
            agentId: 'radiology_agent_1',
            agentType: 'RADIOLOGY',
            status: 'IDLE',
            activeCases: [],
            metrics: {
              casesProcessed: 0,
              averageConfidence: 0,
            },
          },
        ],
      };
    },
  });

  // Get specific agent status
  fastify.get('/:agentId', {
    onRequest: [fastify.authenticate],
    handler: async (request, reply) => {
      const { agentId } = request.params as { agentId: string };

      // TODO: Query specific agent status

      return {
        success: true,
        data: {
          agentId,
          agentType: 'RADIOLOGY',
          status: 'IDLE',
        },
      };
    },
  });

  // Get agent capabilities
  fastify.get('/:agentId/capabilities', {
    onRequest: [fastify.authenticate],
    handler: async (request, reply) => {
      const { agentId } = request.params as { agentId: string };

      // TODO: Get agent capabilities from ontology mapping

      return {
        success: true,
        data: {
          capabilities: [],
        },
      };
    },
  });
};

export default agentsRoutes;
