using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DemoPackage
{
    public class Logger
    {
        public void Log(string className, string methodName, string errorMessage)
        {
            Console.WriteLine("Class name:" + className);
            Console.WriteLine("Method name:" + methodName);
            Console.WriteLine("Error:" + errorMessage);
        }
    }
}