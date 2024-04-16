import {ApiRouteDefinition, GetTokenSilentlyOptions } from '@auth0/auth0-angular';
import { environment as env } from '../environments/environment'

const routes: string[] = [
    '/api/listing',
    '/api/listing/*',
    '/api/payment/*',
    '/api/user',
    '/api/user_check',
    '/api/user/*',
    '/api/conversation/*',
    '/api/conversations',
    '/api/message',
    '/api/conversation',
    '/api/user/messaging',
    '/api/user/listings',
    '/api/user/favourites',
    '/api/user/favourite/*',
]

const options: GetTokenSilentlyOptions = {
    authorizationParams: {
      // The attached token should target this audience
      audience: 'https://buddyfinder.com',

    }
}

export const generateApiRouteDefinitions = (): ApiRouteDefinition[] => {
    let routeDefintions: ApiRouteDefinition[] = []
    for (var route of routes) {
        if (route == '/api/listing/*'){
            routeDefintions.push({httpMethod: 'POST', uri: env.backend_url + route, tokenOptions: options})
            routeDefintions.push({httpMethod: 'DELETE', uri: env.backend_url + route, tokenOptions: options})
            routeDefintions.push({httpMethod: 'PATCH', uri: env.backend_url + route, tokenOptions: options})
        }else {
            routeDefintions.push({uri: env.backend_url + route, tokenOptions: options})
        }
    }
    return routeDefintions
}