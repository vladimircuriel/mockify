import Logout from '@components/logout/Logout'
import { cookies } from 'next/headers'

export default async function LogOut() {
  const jwt = (await cookies()).get('session')?.value
  if (!jwt) {
    return null
  }

  return <Logout />
}
