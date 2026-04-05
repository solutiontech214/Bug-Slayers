import Header from "../components/Header";
import Footer from "../components/Footer";
import Dashboard from "./Dashboard";


const Panel=()=>{
    return(
<div className="bg-gray-950 min-h-screen flex flex-col">
      
     

      <div className="flex-1">
        <Dashboard />
      </div>

     

    </div>
    )
}
export default Panel;